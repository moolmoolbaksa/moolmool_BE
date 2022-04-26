package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkDto {
    private Boolean ok;

    public OkDto(String of){
        if ( of.equals("true")){ this.ok = true; }
        else if ( of.equals("false") ){ this.ok = false; }
    }

    public static OkDto of(String of){ return new OkDto(of); }
}
