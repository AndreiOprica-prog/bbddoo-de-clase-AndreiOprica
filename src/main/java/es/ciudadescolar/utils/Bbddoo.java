package es.ciudadescolar.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import es.ciudadescolar.instituto.Alumno;

public class Bbddoo {

    private static final Logger LOG = LoggerFactory.getLogger(Bbddoo.class);
    private ObjectContainer bd = null;
    private File ficheroBd = null;

    public Bbddoo(File fich, boolean sobrescribir)
    {
        this.ficheroBd=fich;

            if (sobrescribir)
            {
                if(ficheroBd.exists())
                {
                    LOG.warn("Se procede a borrar la BD");
                    ficheroBd.delete();
                }
            }

            bd = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), ficheroBd.getName());
            LOG.debug("Abierta base de datos: "+ficheroBd.getAbsolutePath());

    }

    public boolean cerrar()
    {
        if (bd != null)
        {
            bd.close();
            LOG.debug("Cerrada base de datos");
            return true;
        }
        LOG.warn("No se puede cerrar base de datos no instanciada");
        return false;
    }

    public boolean guardarAlumno(Alumno alumno)
    {
        boolean status = false;

        if (bd != null)
        {
            bd.store(alumno);
            status=true;  
            LOG.info("Guardado alumno: "+alumno);
        }

        return status;
    }

    public List<Alumno> recuperaTodosAlumnos()
    {
        List<Alumno> alumnos =null;
        
        //preparamos una plantilla inclusiva (que todos los objetos Alumno de la BD satisfagan)
        Alumno alumnoBuscado = new Alumno(null, null, null);

        ObjectSet<Alumno> alumnosRecuperados = null;
        
        if (bd != null) 
        {
           alumnosRecuperados = bd.queryByExample(alumnoBuscado);

            if (alumnosRecuperados.size() >0)
            {
                alumnos= new ArrayList<Alumno>();
        
                for (Alumno al:alumnosRecuperados)
                {
                    LOG.info("Alumno recuperado de BD: "+al);
                    alumnos.add(al);
                }
            }
        }        
        return alumnos;
    }

    public Alumno recuperaAlumnoPorExpediente(String exp)
    {
        //preparamos una plantilla con un determinado expediente
        Alumno alumnoBuscado = new Alumno(null, exp, null);
        Alumno alumnoRecuperado=null;
        ObjectSet<Alumno> alumnosRecuperados = null;
        
        if (bd != null) 
        {
           alumnosRecuperados = bd.queryByExample(alumnoBuscado);

            if (alumnosRecuperados.size() == 1)
            {
                alumnoRecuperado = alumnosRecuperados.next();
            }
        }        

        return alumnoRecuperado;
    }

    public boolean borrarAlumno(Alumno alumno) {

        boolean status = false;
        ObjectSet<Alumno> alumnosRecuperados = null;
        if (bd == null) {
            alumnosRecuperados = bd.queryByExample(alumno);
            int totalRecuperados = alumnosRecuperados.size();
            
            if (totalRecuperados == 1) {
                bd.delete(alumno);
                status = true;
                LOG.info("Borrado el alumno: " + alumno);
            } else {
                if (totalRecuperados == 0) {
                    status = false;
                    LOG.warn("No se ha localizado objeto a borrar: " + alumno);
                } else {
                    LOG.warn("Se han localizado varios objetos a borrar: " + totalRecuperados);
                }
            }
        }

        return status;

    }

    public boolean borrarTodosLosAlumnos() {
        boolean status = true;

        List<Alumno> alumnosABorrar = recuperaTodosAlumnos();

        if (alumnosABorrar.size() == 0) {
            LOG.warn("No hay objetos alumnos a borrar.");
            status = false;
        }

        for (Alumno al: alumnosABorrar) {
            bd.delete(al);
            LOG.debug("Borrado el alumno: " + al);
        }

        return status;
    }
    public boolean modificarEdadAlumno(Alumno alumnoAModificar, Integer edadNueva) {
        boolean status = false;

        ObjectSet<Alumno> alumnosAModificar = bd.queryByExample(alumnoAModificar);

        Alumno alumno = null;

        int totalAlumnosAModificar = alumnosAModificar.size();

        switch(totalAlumnosAModificar) {
            case 0:
                    LOG.warn("No existe alumno en la BD");
                break;
            case 1:
                    alumno = alumnosAModificar.next();
                    alumno.setEdad(edadNueva);
                    bd.store(alumno);

                    // O también: 
                    // alumnoAModificar.setEdad(edadNueva);
                    // bd.store(alumnoAModificar);
                    
                    LOG.info("Se ha actualizado la edad del alumno: " + alumno);
                    status = true;
                break;
            default:
                    LOG.warn("No se actualizan varios objetos de una vez");
                break;
            
        }

        return status;
    }

    public void commitTransaction() {
        bd.commit();
        LOG.debug("Se consolidan los cambios previos en la base de datos");
    }

    public void rollbackTransaction() {
        bd.rollback();
        LOG.debug("Se han descartado los cambios previos en la base de datos");
    }
}
