// UserRegistrationDto.java
package com.example.LabelApp.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    @NotBlank(message = "Пароль обязателен")
    private String password;
}