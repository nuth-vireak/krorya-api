package com.kshrd.krorya.convert;

import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.entity.AppUser;
import com.kshrd.krorya.model.entity.CustomUserDetail;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AppUserConvertor {

    private ModelMapper modelMapper;

    public AppUserDTO toDTO(AppUser appUser) {
        return modelMapper.map(appUser, AppUserDTO.class);
    }

    public AppUser toEntity(AppUserDTO appUserDTO) {
        return modelMapper.map(appUserDTO, AppUser.class);
    }
}
