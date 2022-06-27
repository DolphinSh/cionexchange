package com.dolphin.mappers;

import com.dolphin.domain.AdminBank;
import com.dolphin.dto.AdminBankDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminBankDtoMappers {

    AdminBankDtoMappers INSTANCE = Mappers.getMapper(AdminBankDtoMappers.class);
    /**
     * dto 转 Entity
     * @param source
     * @return
     */
    AdminBank toConvertEntity(AdminBankDto source);

    /**
     * entity 转 dto
     * @param source
     * @return
     */
    AdminBankDto toConvertDto(AdminBank source);

    /**
     * List
     * dto 转 Entity
     * @param source
     * @return
     */
    List<AdminBank> toConvertEntity(List<AdminBankDto> source);

    /**
     * list
     * entity 转 dto
     * @param source
     * @return
     */
    List<AdminBankDto> toConvertDto(List<AdminBank> source);
}
