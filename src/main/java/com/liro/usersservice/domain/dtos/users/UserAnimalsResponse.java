package com.liro.usersservice.domain.dtos.users;

import com.liro.usersservice.domain.dtos.animals.AnimalCompleteResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserAnimalsResponse extends  UserResponse{

    private List<AnimalCompleteResponse> animals;
}
