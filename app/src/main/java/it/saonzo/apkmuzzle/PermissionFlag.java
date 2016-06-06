package it.saonzo.apkmuzzle;

public class PermissionFlag {
    private String name;
    private boolean checked;

    PermissionFlag (String name){
        this.name = name;
        this.checked = false;
    }

    PermissionFlag (String name, boolean checked){
        this.name = name;
        this.checked = checked;
    }

    public String getName(){
        return this.name;
    }

    public void setChecked(boolean checked) { this.checked = checked; }

    public boolean isChecked() {
        return this.checked;
    }

}

