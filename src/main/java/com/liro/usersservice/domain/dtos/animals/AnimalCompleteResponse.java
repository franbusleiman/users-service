package com.liro.usersservice.domain.dtos.animals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AnimalCompleteResponse extends AnimalDTO {

    private Long id;

    private AnimalTypeResponse animalType;

}