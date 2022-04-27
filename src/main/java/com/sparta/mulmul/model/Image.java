package com.sparta.mulmul.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String imgUrl;

    public  Image(String fileName, String imgUrl){
        this.fileName = fileName;
        this.imgUrl = imgUrl;
    }
}
