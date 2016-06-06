package it.saonzo.apkmuzzle;

import java.io.File;

public interface IFolderItemListener {
    void OnCannotFileRead(File file); // what to do folder is unreadable
    void OnFileClicked(File file); // what to do when a file is clicked
}