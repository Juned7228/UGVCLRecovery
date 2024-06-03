package com.bytecodesolutions.ugvclrecovery.model;

public class Consumer {
    int id;
    String num;
    String name;
    String address1;
    String tarif;
    String type;
    String meterno;
    String rootcode;
    String mobile;
    String amount;
    String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeterno() {
        return meterno;
    }

    public void setMeterno(String meterno) {
        this.meterno = meterno;
    }



    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }



    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRootcode() {
        return rootcode;
    }

    public void setRootcode(String rootcode) {
        this.rootcode = rootcode;
    }



    public Consumer( String num, String name,String address1, String tarif,String meterno,String rootcode, String amount,String mobile,String type,String remarks) {

        this.num = num;
        this.name = name;
        this.address1=address1;
        this.tarif = tarif;
        this.meterno=meterno;
        this.rootcode=rootcode;
        this.amount = amount;
        this.mobile=mobile;
        this.type=type;
        this.remarks=remarks;
    }
    public Consumer(int id, String num, String name,String address1, String tarif,String meterno,String rootcode, String amount,String mobile,String type,String remarks) {
        this.id=id;
        this.num = num;
        this.name = name;
        this.address1=address1;
        this.tarif = tarif;
        this.meterno=meterno;
        this.rootcode=rootcode;
        this.amount = amount;
        this.mobile=mobile;
        this.type=type;
        this.remarks=remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }




}
