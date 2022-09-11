package com.pablobuendia.universitymanager.entities.student;

import com.pablobuendia.universitymanager.error.ElementNotFoundException;
import com.pablobuendia.universitymanager.event.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "product-cache")
public class StudentService {

    private final StudentRepository studentRepository;

    private final Publisher publisher;

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper.INSTANCE::studentToStudentDto)
                .collect(Collectors.toList());
    }

    @Cacheable
    public StudentDto getStudent(Long id) {
        val student = studentRepository.findById(id).orElseThrow(() ->
                new ElementNotFoundException("No such student found with id " + id));
        return StudentMapper.INSTANCE.studentToStudentDto(student);
    }

    public StudentDto addStudent(StudentDto studentDto) {
        val studentEntity = StudentMapper.INSTANCE.studentDtoToStudent(studentDto);

        val savedStudent = studentRepository.save(studentEntity);

        publisher.publishEvent(savedStudent.getUpdated().toString());

        return StudentMapper.INSTANCE.studentToStudentDto(savedStudent);
    }

    @CacheEvict
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id))
            throw new ElementNotFoundException("No such student found with id " + id);

        studentRepository.deleteById(id);
    }

    @CachePut(key = "#id")
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        if (!studentRepository.existsById(id))
            throw new ElementNotFoundException("No such student found with id " + id);

        val updatedStudent = studentRepository
                .save(StudentMapper.INSTANCE.studentDtoToStudent(studentDto));
        return StudentMapper.INSTANCE.studentToStudentDto(updatedStudent);
    }
}
