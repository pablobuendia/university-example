package com.example.universityexample.universityexample.address;

import com.example.universityexample.universityexample.BaseEntityDto;
import com.example.universityexample.universityexample.address.city.CityDto;
import lombok.Data;

@Data
public class AddressDto {

    private String id;
    private String lineStreet1;
    private String lineStreet2;
    private BaseEntityDto owner;
    private CityDto city;
}
