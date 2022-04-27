package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OkDto {
    private Boolean ok;

    public OkDto(String value){
        if ( value.equals("true")){ this.ok = true; }
        else if ( value.equals("false") ){ this.ok = false; }
    }

    public static OkDto valueOf(String value){ return new OkDto(value); }
}
