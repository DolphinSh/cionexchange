package com.dolphin.mappers;

import com.dolphin.domain.User;
import com.dolphin.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用来做对象的映射化
 */
@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    //获取该对象的实例
    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    /**
     * 将entity转换为dto
     * @param source 源对象
     * @return
     */
    UserDto convert2Dto(User source);

    /**
     * 将dto对象转换为entity对象
     * @param source 源对象
     * @return
     */
    User convert2Entity(UserDto source);

    /**
     * 将entity转换为dto
     * @param source 源对象
     * @return
     */
    List<UserDto> convert2Dto(List<User> source);

    /**
     * 将List<dto>对象转换为entity对象
     * @param source 源对象
     * @return
     */
    List<User> convert2Entity(List<UserDto> source);
}
