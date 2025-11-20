package es.ciudadescolar;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ciudadescolar.instituto.Alumno;
import es.ciudadescolar.utils.Bbddoo;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger("Main.class");
    public static void main(String[] args) {
        
        Bbddoo bd = new Bbddoo(new File("alumnos.db4o"), true);

        Alumno al1 = new Alumno("Paco", "9999", Integer.valueOf(23));
        Alumno al2 = new Alumno("Fermín", "8888", Integer.valueOf(20));
        Alumno al3 = new Alumno("David", "7777", Integer.valueOf(19));

        bd.guardarAlumno(al1);
        bd.guardarAlumno(al2);
        bd.guardarAlumno(al3);

        List<Alumno> alumnosBd = bd.recuperaTodosAlumnos();

        LOG.info(bd.recuperaAlumnoPorExpediente("8888").toString());

        if (!bd.borrarAlumno(al1)) {
            LOG.error("No se ha podido borrar el alumno: " + al1);
        }

        bd.borrarTodosLosAlumnos();

        LOG.info("Tras borrar todos los alumnos, los objetos Alumno de la BD son: ");
        if (bd.recuperaTodosAlumnos() == null) {
            LOG.warn("No quedan instancias de la clase alumno en la BD");
        }

        Alumno al4 = new Alumno("Dario", "2222", Integer.valueOf(23));
        Alumno al5 = new Alumno("Maria", "444444", Integer.valueOf(24));

        // Si nos exigen el guardado de amnos alumnos de forma trnsaccional (todos o ninguno)
        try {
            bd.guardarAlumno(al4);
            if (true ) {
                throw new Exception();
            }
            bd.guardarAlumno(al5);
            bd.commitTransaction();
        } catch (Exception e) {
            bd.rollbackTransaction();
            LOG.error("Imposible guardar transaccionalmente los alumnos: " + e.getMessage());
        }

        bd.modificarEdadAlumno(al5, Integer.valueOf(20));

        LOG.info("Tras la actualización de la alumna, los objetos Alumno de la BD son:");
        if (bd.recuperaTodosAlumnos() == null) {
            LOG.warn("No quedan instancias de la clase Alumno en la BD.");
        }

        bd.cerrar();

    }
}
