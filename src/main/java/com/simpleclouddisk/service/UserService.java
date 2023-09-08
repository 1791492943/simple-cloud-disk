package com.simpleclouddisk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.dto.UserFileDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-08-07 08:52:13
*/
public interface UserService extends IService<User> {

    /**
     * 生成短信验证码
     * @param phone
     */
    void code(String phone);

    /**
     * 登录
     * @param user
     */
    String login(UserLoginDto user) throws ServiceException;

    /**
     * 修改密码
     * @param user
     */
    void updatePassword(User user) throws ServiceException;

    Map<String, Long> getSpace();


    Page page(FilePageDto filePageDto);

    void deleteFileById(Long[] fileIds);

    void rename(Long id, String fileName);

    void deleteFileInfoById(List<Long> list);

    List<UploadRecordsDto> uploadInfo();

    void restore(Long[] fileIds);

    void newFolder(Long pid, String folderName);

    List<UserFileDto> search(String name);

    void move(List<Long> ids, Long pid);

    List<UserFileDto> folder(Long pid);
}
