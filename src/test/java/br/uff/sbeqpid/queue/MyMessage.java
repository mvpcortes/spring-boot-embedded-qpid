package br.uff.sbeqpid.queue;

import java.io.Serializable;

public class MyMessage implements Serializable {

    private String name;

    private Long id;

    public MyMessage() {
    }

    public MyMessage(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public MyMessage setName(String name) {
        this.name = name;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id ){
        this.id = id;
    }

}
