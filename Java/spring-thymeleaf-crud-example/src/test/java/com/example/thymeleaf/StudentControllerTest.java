package com.example.thymeleaf;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import com.example.thymeleaf.controller.StudentController;
import com.example.thymeleaf.dto.CreateStudentDTO;

public class StudentControllerTest {

    private StudentController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentController(null, null);
    }

    private BindingResult bindingResult() {
        return new MapBindingResult(new HashMap<>(), "student");
    }

    @Test
    void testUnsafeName() {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setName("><img src=x onerror=\"alert(' hacking ')\"><a hre=\"a");
        dto.setEmail("abc@example.com");

        BindingResult result = bindingResult();
        boolean unsafe = invokeContainsUnsafeInput(dto, result);

        assertTrue(unsafe);
        assertTrue(result.hasErrors());
    }

    @Test
    void testUnsafeStreet() {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setName("monika");
        dto.setEmail("abc@example.com");
        dto.setStreet("javascript:alert('xss')");

        BindingResult result = bindingResult();
        boolean unsafe = invokeContainsUnsafeInput(dto, result);

        assertTrue(unsafe);
        assertTrue(result.hasErrors());
    }

    @Test
    void testSafeInput() {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setName("monika");
        dto.setEmail("abc@example.com");

        BindingResult result = bindingResult();
        boolean unsafe = invokeContainsUnsafeInput(dto, result);

        assertFalse(unsafe);
        assertFalse(result.hasErrors());
    }

    private boolean invokeContainsUnsafeInput(CreateStudentDTO dto, BindingResult result) {
        try {
            var method = StudentController.class.getDeclaredMethod("containsUnsafeInput", CreateStudentDTO.class,
                    BindingResult.class);
            method.setAccessible(true);
            return (boolean) method.invoke(controller, dto, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
