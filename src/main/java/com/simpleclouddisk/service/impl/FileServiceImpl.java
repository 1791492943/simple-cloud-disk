package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.code.FileCode;
import com.simpleclouddisk.config.MinioConfig;
import com.simpleclouddisk.domain.dto.FileShardDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.domain.entity.FileShard;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.exception.service.SpaceException;
import com.simpleclouddisk.mapper.FileShardMapper;
import com.simpleclouddisk.mapper.UserFileMapper;
import com.simpleclouddisk.mapper.UserMapper;
import com.simpleclouddisk.service.FileService;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.utils.FileTypeUtil;
import com.simpleclouddisk.utils.MinioUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【file_info】的数据库操作Service实现
 * @createDate 2023-08-08 15:19:24
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileShardMapper fileShardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFileMapper userFileMapper;

    /**
     * 上传文件
     *
     * @param file
     * @throws Exception
     */
    @Override
    public void upload(MultipartFile file, String fileShard) throws Exception {
        // 计算空间
        long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user.getUseSpace() + file.getSize() > user.getTotalSpace()) throw new SpaceException("空间不足请扩容");
        userMapper.setSpace(userId, file.getSize());

        FileShardDto fileShardDto = JSON.parseObject(fileShard, FileShardDto.class);
        // 分片存在
        if (FileShardExist(fileShardDto)) {
            return;
        }

        // 分片保存
        String shardPath = MinioConfig.shardPath + "/" + fileShardDto.getShardName();
        file.transferTo(new File(shardPath));

        FileShard fs = new FileShard();
        BeanUtils.copyProperties(fileShardDto, fs);
        fs.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Long loginIdAsString = StpUtil.getLoginIdAsLong();
        fs.setUserId(loginIdAsString);

        fileShardMapper.insert(fs);

        // 最后一个分片
        if (fileShardDto.getLast() == 1) {
            // 查询该文件所有分片
            LambdaQueryWrapper<FileShard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper
                    .eq(FileShard::getFileMd5, fileShardDto.getFileMd5())
                    .orderByAsc(FileShard::getCurrentShard);
            List<FileShard> list = fileShardMapper.selectList(queryWrapper);

            // 临时储存文件
            File temporaryFile = new File(MinioConfig.shardPath + "/" + UUID.randomUUID() + fileShardDto.getSuffixName());
            temporaryFile.createNewFile();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(temporaryFile));
            BufferedInputStream inputStream = null;

            // 分片合并
            for (FileShard shard : list) {
                File shardFile = new File(MinioConfig.shardPath + "/" + shard.getShardName());
                inputStream = new BufferedInputStream(new FileInputStream(shardFile));
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                shardFile.delete();
            }
            // 释放资源
            outputStream.close();

            // 上传minio
            String fileMinioName = MinioUtil.upload(temporaryFile, fileShardDto.getSuffixName());

            int fileType = FileCode.CATEGORY_OTHER;
            // 图片
            if(FileTypeUtil.isImageFile(fileShardDto.getSuffixName())){
                // 图片
                File outputThumbnailFile = new File( MinioConfig.thumbnail + "/" + fileShardDto.getFileMd5() + ".jpg"); // 缩略图输出路径
                // 生成缩略图，设置大小为 30x30 像素
                Thumbnails.of(temporaryFile)
                        .size(30, 30)
                        .toFile(outputThumbnailFile);
                fileType = FileCode.CATEGORY_IMAGE;
            }
            // 音乐
            if(FileTypeUtil.isMusicFile(fileShardDto.getSuffixName())){
                fileType = FileCode.CATEGORY_AUDIO;
            }
            // 视频
            if(FileTypeUtil.isVideoFile(fileShardDto.getSuffixName())){
                fileType = FileCode.CATEGORY_VIDEO;
            }

            // 保存信息
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            FileInfo fileInfo = FileInfo.builder()
                    .fileMd5(fileShardDto.getFileMd5())
                    .fileSize(temporaryFile.length())
                    .fileName(fileMinioName)
                    .fileCover(fileShardDto.getFileMd5() + ".jpg")
                    .fileCategory(fileType)
                    .createTime(timestamp)
                    .updateTime(timestamp)
                    .build();
            this.save(fileInfo);

            // 保存用户文件信息
            UserFile userFile = UserFile.builder()
                    .userId(StpUtil.getLoginIdAsLong())
                    .fileId(fileInfo.getFileId())
                    .minioName(temporaryFile.getName())
                    .fileName(fileShardDto.getFileName())
                    .filePid(fileShardDto.getFilePid())
                    .folderType(FileCode.TYPE_FILE)
                    .fileSize(file.getSize())
                    .delFlag(FileCode.DEL_NO)
                    .createTime(timestamp)
                    .updateTime(timestamp)
                    .build();
            userFileMapper.insert(userFile);

            // 删除零时文件
            temporaryFile.delete();

            // 删除分片信息
            fileShardMapper.delete(new LambdaQueryWrapper<FileShard>().eq(FileShard::getFileMd5, fileShardDto.getFileMd5()));
        }


    }

    /**
     * 在线预览
     *
     * @throws Exception
     */
    @Override
    public String preview(Long fileId) throws Exception {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        String fileName = fileInfo.getFileName();
        return MinioUtil.preview(fileName);
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @throws Exception
     */
    @Override
    public void delete(String fileName) throws Exception {
        MinioUtil.delete(fileName);
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param outputStream
     * @throws Exception
     */
    @Override
    public void download(String fileName, OutputStream outputStream) throws Exception {
        MinioUtil.download(fileName, outputStream);
    }

    @Override
    public Map fileExist(String fileMd5) {
        FileInfo fileInfo = this.getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileMd5, fileMd5));
        HashMap<String, String> map = new HashMap<>();
        map.put("exist", String.valueOf(Objects.nonNull(fileInfo)));
        if(Objects.nonNull(fileInfo)){
            map.put("name",fileInfo.getFileName());
        }
        return map;
    }

    @Override
    public boolean shardExist(String shardMd5) {
        FileShard fileShard = fileShardMapper.selectOne(new LambdaQueryWrapper<FileShard>().eq(FileShard::getShardMd5, shardMd5));
        return fileShard != null;
    }

    /**
     * 文件是否存在
     *
     * @param fileShard
     */
    private FileInfo FileExist(FileShardDto fileShard) {
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFileMd5, fileShard.getFileMd5());
        FileInfo fileInfo = fileMapper.selectOne(queryWrapper);
        return fileInfo;
    }

    /**
     * 文件分片是否存在
     *
     * @param fileShard
     */
    private boolean FileShardExist(FileShardDto fileShard) {
        LambdaQueryWrapper<FileShard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileShard::getShardMd5, fileShard.getShardMd5());
        FileShard fs = fileShardMapper.selectOne(queryWrapper);
        return Objects.nonNull(fs);
    }

}




