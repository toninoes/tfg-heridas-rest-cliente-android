package uca.ruiz.antonio.tfgapp.utils;

import java.text.DateFormat;
import java.util.Locale;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uca.ruiz.antonio.tfgapp.data.api.model.Cita;
import uca.ruiz.antonio.tfgapp.data.api.model.SalaConfig;

import static uca.ruiz.antonio.tfgapp.R.string.minutos_paciente;
import static uca.ruiz.antonio.tfgapp.R.string.orden;


public class FechaHoraUtils {
    private static final String UI_FECHA_HORA = "dd/MM/yyyy HH:mm";
    private static final String UI_FECHA = "dd/MM/yyyy";
    private static final String API_FECHA = "yyyy-MM-dd";
    private static final String UI_HORA = "h:mma";
    private static final String UI_HORA2 = "hh:mm";
    private static final String API_HORA = "HH:mm:ss";
    private static final String API_FECHA_HORA = "yyyy-MM-dd HH:mm:ss";
    private static final String FILE_FECHA_HORA = "yyyy-MM-dd_HHmmssSSS";

    private FechaHoraUtils() {
    }

    public static String dosDigitos(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public static Date FechaSinTiempo(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date getHoySinTiempo() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date getFechaFromString(String txt) {
        SimpleDateFormat format = new SimpleDateFormat(UI_FECHA, Locale.getDefault());
        Date fecha = new Date();
        try {
            fecha = format.parse(txt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha;
    }


    public static Date getFechaActual() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTime();
    }

    public static String formatoFechaHoraUI(int year, int month, int dayOfMonth) {
        return formatoFechaHoraUI(crearFecha(year, month, dayOfMonth));
    }

    public static String formatoFechaHoraUI(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_FECHA_HORA, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatoFileFechaHoraUI(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FILE_FECHA_HORA, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatoFechaUI(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_FECHA, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static Date crearFecha(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String formatoFechaAPI(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(API_FECHA, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatoHoraUI(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(API_HORA, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(time);
            simpleDateFormat.applyPattern(UI_HORA);
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String formatoHoraUI(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_HORA2, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static Date parseoHoraUI(String timeSchedule) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UI_HORA, Locale.getDefault());
        try {
            return simpleDateFormat.parse(timeSchedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String unirFechaHora(Date datePicked, Date timeUi) {
        Calendar datePickedCal = Calendar.getInstance();
        Calendar timeUiCal = Calendar.getInstance();

        datePickedCal.setTime(datePicked);
        timeUiCal.setTime(timeUi);

        datePickedCal.add(Calendar.HOUR_OF_DAY, timeUiCal.get(Calendar.HOUR_OF_DAY));
        datePickedCal.add(Calendar.MINUTE, timeUiCal.get(Calendar.MINUTE));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(API_FECHA_HORA, Locale.getDefault());
        return simpleDateFormat.format(datePickedCal.getTime());
    }

    public static Integer getEdad(Date nacimiento) {
        Calendar fnac = Calendar.getInstance();
        Calendar ahora = Calendar.getInstance();

        fnac.setTime(nacimiento);
        ahora.setTime(new Date());

        ahora.add(Calendar.DAY_OF_YEAR, 1 - fnac.get(Calendar.DAY_OF_YEAR));

        return ahora.get(Calendar.YEAR) - fnac.get(Calendar.YEAR);
    }

    public static String getNacimientoAndEdad(Date date) {
        return formatoFechaUI(date) + " (" + getEdad(date).toString() + ")";
    }


    public static Calendar DateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date calcularFechaHoraCita(Date dia, Integer horaini, Integer minini, Long orden, Long minutos_paciente) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dia);
        cal.set(Calendar.HOUR_OF_DAY, horaini);
        cal.set(Calendar.MINUTE, minini);

        Integer sumar = (orden.intValue()-1) * minutos_paciente.intValue();
        cal.add(Calendar.MINUTE, sumar);

        return cal.getTime();
    }

    public static Date calcularFechaHoraCita(Cita cita) {
        SalaConfig sC = cita.getSala().getSalaConfig();
        Date dia = cita.getFecha();
        Long orden = cita.getOrden();
        Integer horaini = sC.getHoraini();
        Integer minini = sC.getMinini();
        Long minutos_paciente = sC.getMinutosPaciente();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dia);
        cal.set(Calendar.HOUR_OF_DAY, horaini);
        cal.set(Calendar.MINUTE, minini);

        Integer sumar = (orden.intValue()-1) * minutos_paciente.intValue();
        cal.add(Calendar.MINUTE, sumar);

        return cal.getTime();
    }

    public static String calcularFechaHoraCitaToString(Cita cita) {
        SalaConfig sC = cita.getSala().getSalaConfig();
        Date dia = cita.getFecha();
        Long orden = cita.getOrden();
        Integer horaini = sC.getHoraini();
        Integer minini = sC.getMinini();
        Long minutos_paciente = sC.getMinutosPaciente();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dia);
        cal.set(Calendar.HOUR_OF_DAY, horaini);
        cal.set(Calendar.MINUTE, minini);

        Integer sumar = (orden.intValue()-1) * minutos_paciente.intValue();
        cal.add(Calendar.MINUTE, sumar);

        return formatoFechaHoraUI(cal.getTime());
    }
}

