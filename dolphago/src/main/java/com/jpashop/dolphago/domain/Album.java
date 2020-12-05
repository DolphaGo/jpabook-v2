package com.jpashop.dolphago.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Album extends Item {
    String artist;
    String etc;
}
