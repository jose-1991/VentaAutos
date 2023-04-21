package com.practice.jose;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PrestamoTest {
    private Persona solicitante1;
    private Persona solicitante2;
    private Persona garante1;
    private Persona garante2;
    private Inmueble inmueble;
    private List<Persona> solicitantes;
    private List<Persona> garantes;

    @InjectMocks
    private Prestamo prestamo;

    @Before
    public void setUp(){
        inmueble = new Inmueble(60000);
        solicitante1 = new Persona(true, 6000, inmueble);
        solicitante2 = new Persona(true, 2000,inmueble);
        garante1  = new Persona(false, 25000, inmueble);
        garante2 = new Persona(false,200,inmueble);
    }
    @Test
    public void testEsElegibleCuandoHayUnSolicitanteYNoTieneGarantes(){
        
        solicitantes = Collections.singletonList(solicitante1);

        boolean esElegible = prestamo.esElegible(solicitantes, null,35000,false,false);
        assertTrue(esElegible);
    }

    @Test
    public void testEsElegibleCuandoHayUnSolicitanteYTieneUnGarante(){
        solicitante1.setAhorro(3000);
        solicitantes = Collections.singletonList(solicitante1);
        garantes = Collections.singletonList(garante1);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,false);
        assertTrue(esElegible);
    }

    @Test
    public void testEsElegibleCuandoHayUnSolicitanteYTieneDosGarante(){
        garante1.setAhorro(6000);
        garante1.setTieneTrabajo(true);
        solicitantes = Collections.singletonList(solicitante1);
        garantes = Arrays.asList(garante1,garante2);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,false);
        assertTrue(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayUnSolicitanteYnoTieneGarantes(){
        solicitante1.setAhorro(6000);
        solicitante1.setInmueble(null);
        solicitantes = Collections.singletonList(solicitante1);

        boolean esElegible = prestamo.esElegible(solicitantes, null,35000,false,false);
        assertFalse(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayUnSolicitanteYTieneUnGarante(){
        solicitante1.setAhorro(3000);
        solicitante1.setInmueble(inmueble);

        garante1.setAhorro(2000);
        garante1.setTieneTrabajo(false);

        solicitantes = Collections.singletonList(solicitante1);
        garantes = Collections.singletonList(garante1);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,false);
        assertFalse(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayUnSolicitanteYTieneDosGarante(){
        solicitante1.setAhorro(2000);

        garante1.setAhorro(4000);
        garante1.setInmueble(null);
        garante2.setInmueble(null);
        solicitantes = Collections.singletonList(solicitante1);
        garantes = Arrays.asList(garante1,garante2);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,false);
        assertFalse(esElegible);
    }

    @Test
    public void testEsElegibleCuandoHayDosSolicitantesYNoTieneGarantes(){
        solicitante1.setAhorro(6000);

        solicitantes = Arrays.asList(solicitante1, solicitante2);

        boolean esElegible = prestamo.esElegible(solicitantes, null,35000,true,false);
        assertTrue(esElegible);
    }

    @Test
    public void testEsElegibleCuandoHayDosSolicitanteYTieneUnGarante(){
        solicitante1.setAhorro(2000);
        solicitante2.setAhorro(2000);
        solicitante2.setInmueble(inmueble);


        garante1.setAhorro(25000);

        solicitantes = Arrays.asList(solicitante1, solicitante2);
        garantes = Collections.singletonList(garante1);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,true,false);
        assertTrue(esElegible);
    }

    @Test
    public void testEsElegibleCuandoHayDosSolicitanteYTieneDosGarante(){

        garante1.setTieneTrabajo(true);
        garante1.setAhorro(6000);
        garante2.setInmueble(inmueble);
        solicitantes = Arrays.asList(solicitante1, solicitante2);
        garantes = Arrays.asList(garante1,garante2);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,false);
        assertTrue(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayDosSolicitanteYnoTieneGarantes(){
        solicitante1.setInmueble(null);
        solicitante2.setInmueble(null);

        solicitantes = Arrays.asList(solicitante1, solicitante2);

        boolean esElegible = prestamo.esElegible(solicitantes, null,35000,false,false);
        assertFalse(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayDosSolicitanteYTieneUnGarante(){
        solicitante1.setAhorro(1000);
        solicitante1.setInmueble(inmueble);
        solicitante2.setInmueble(inmueble);

        garante1.setAhorro(2000);
        garante1.setInmueble(null);

        solicitantes = Arrays.asList(solicitante1, solicitante2);
        garantes = Collections.singletonList(garante1);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,true,false);
        assertFalse(esElegible);
    }

    @Test
    public void testNoEsElegibleCuandoHayDosSolicitanteYTieneDosGarante(){
        solicitante1.setAhorro(300);

        garante1.setAhorro(3000);


        solicitantes = Arrays.asList(solicitante1, solicitante2);
        garantes = Arrays.asList(garante1,garante2);

        boolean esElegible = prestamo.esElegible(solicitantes, garantes,35000,false,true);
        assertFalse(esElegible);
    }
}