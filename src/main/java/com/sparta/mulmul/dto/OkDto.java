package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkDto {
    private Boolean ok;

    public OkDto(String of){
        if ( of.equals("ok")){ this.ok = true;}
    }

    public static OkDto of(String of){
        return new OkDto(of);
    }
}
