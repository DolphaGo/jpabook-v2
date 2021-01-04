package com.jpashop.dolphago.domain;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Album extends Item {
    String artist;
    String etc;
}
