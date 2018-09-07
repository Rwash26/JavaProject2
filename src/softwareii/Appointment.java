/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import java.time.LocalDateTime;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Flash
 */
public class Appointment {
    
    private ObservableValue<String> appointmentId;
    private ObservableValue<String> title;
    private ObservableValue<LocalDateTime> start;
    private ObservableValue<LocalDateTime> end;
    private ObservableValue<String> customerId;

    public Appointment(ObservableValue<String> appointmentId, ObservableValue<String> title, ObservableValue<LocalDateTime> start, ObservableValue<LocalDateTime> end,  ObservableValue<String> customerId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.start = start;
        this.end = end;
        this.customerId = customerId;

    }
    
     public ObservableValue<String> getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentID(ObservableValue<String> appointmentId) {
        this.appointmentId = appointmentId;
    }
    
     public ObservableValue<String> getTitle() {
        return title;
    }

    public void setTitle(ObservableValue<String> title) {
        this.title = title;
    }

    public ObservableValue<LocalDateTime> getStart() {
        return start;
    }

    public void setStart(ObservableValue<LocalDateTime> start) {
        this.start = start;
    }
     public ObservableValue<LocalDateTime> getEnd() {
        return end;
    }

    public void setEnd(ObservableValue<LocalDateTime> end) {
        this.end = end;
    }

    public ObservableValue<String> getCustomerId() {
        return customerId;
    }

    public void setCustomerId(ObservableValue<String> customerId) {
        this.customerId = customerId;
    }
    
  
}
