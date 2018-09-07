/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareii;

import javafx.beans.value.ObservableValue;

/**
 *
 * @author amy.antonucci
 */
public class TableRow {
    private ObservableValue<String> customerID;
    private ObservableValue<String> customerName;
    private ObservableValue<String> address;
    private ObservableValue<String> city;
    private ObservableValue<String> country;
    private ObservableValue<String> postalCode;
    private ObservableValue<String> phone;

    public TableRow(ObservableValue<String> customerID, ObservableValue<String> customerName, ObservableValue<String> address, ObservableValue<String> city, ObservableValue<String> country, ObservableValue<String> postalCode,ObservableValue<String> phone) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
    }
    
     public ObservableValue<String> getCustomerID() {
        return customerID;
    }

    public void setCustomerID(ObservableValue<String> customerID) {
        this.customerID = customerID;
    }
    
     public ObservableValue<String> getCustomerName() {
        return customerName;
    }

    public void setCustomerName(ObservableValue<String> customerName) {
        this.customerName = customerName;
    }
    
     public ObservableValue<String> getAddress() {
        return address;
    }

    public void setAddress(ObservableValue<String> address) {
        this.address = address;
    }

    public ObservableValue<String> getCity() {
        return city;
    }

    public void setCity(ObservableValue<String> city) {
        this.city = city;
    }

    public ObservableValue<String> getCountry() {
        return country;
    }

    public void setCountry(ObservableValue<String> country) {
        this.country = country;
    }

    public ObservableValue<String> getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(ObservableValue<String> postalCode) {
        this.postalCode = postalCode;
    }

    public ObservableValue<String> getPhone() {
        return phone;
    }

    public void setPhone(ObservableValue<String> phone) {
        this.phone = phone;
    }
    
    
}
