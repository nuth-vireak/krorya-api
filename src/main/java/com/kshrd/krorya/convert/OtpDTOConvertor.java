package com.kshrd.krorya.convert;

import com.kshrd.krorya.model.dto.AppUserDTO;
import com.kshrd.krorya.model.dto.OtpDTO;
import com.kshrd.krorya.model.entity.AppUser;
import com.kshrd.krorya.model.entity.Otp;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OtpDTOConvertor {
    private ModelMapper modelMapper;

    public OtpDTO toDTO(Otp otp) {
        OtpDTO dto = new OtpDTO();
        dto.setOtpCode(otp.getOtpCode());
        dto.setIssuedDate(otp.getIssuedAt());
        dto.setExpiresAt(otp.getExpiresAt());
        dto.setVerify(otp.isVerify());
        dto.setAppUserDTO(modelMapper.map(otp.getAppUser(), AppUserDTO.class)); // Map the user separately
        dto.setUserId(otp.getAppUser().getUserId());
        dto.setVerifiedForget(otp.isVerifiedForget());
        return dto;
    }

    public Otp toEntity(OtpDTO dto) {
        Otp otp = new Otp();
        otp.setOtpCode(dto.getOtpCode());
        otp.setIssuedAt(dto.getIssuedDate());
        otp.setExpiresAt(dto.getExpiresAt());
        otp.setVerify(dto.isVerify());
        otp.setAppUser(modelMapper.map(dto.getAppUserDTO(), AppUser.class)); // Map the user separately
        otp.setVerifiedForget(dto.isVerifiedForget());
        return otp;
    }
}

