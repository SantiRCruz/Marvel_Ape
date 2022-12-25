package com.example.mapes.core

import org.junit.Assert.*
import org.junit.Test

class ValidationsTest{
    @Test
    fun `emailIsEmptyAndReturnFalse`() {
        val result = Validations.validateEmail("")
        assertEquals(false, result)
    }

    @Test
    fun `emailIsNullAndReturnFalse`() {
        val result = Validations.validateEmail(null)
        assertEquals(false, result)
    }

    @Test
    fun `emailCorrectAndReturnTrue`() {
        val result = Validations.validateEmail("santiago@gmail.com")
        assertEquals(true, result)
    }

    @Test
    fun `somethingIsNullAndReturnFalse`(){
        val result = Validations.validateEmpty(null)
        assertEquals(false,result)
    }
    @Test
    fun `somethingIsEmptyAndReturnFalse`(){
        val results = Validations.validateEmpty("")
        assertEquals(false,results)
    }
    @Test
    fun `passwordIsCorrectAndReturnTrue`(){
        val result = Validations.validateEmpty("1234")
        assertEquals(true,result)
    }
}