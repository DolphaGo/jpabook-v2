package com.jpashop.dolphago.domain.shop;

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
