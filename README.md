# Gitlet - A Simplified Version Control System

This repository contains a simplified version of Git. The project allows you to track changes to files, create commits, and manage branches. The implementation uses Java serialization for persisting data and follows a structure similar to Git.

## Features

- **Repository Initialization (`init`)**
  - Creates a new Gitlet version-control system in the current directory.
  - Initializes the directory structure, including branches, commits, blobs, and a staging area.

- **Staging Files (`add`)**
  - Adds files to the staging area for the next commit.
  - Avoids adding files if they are identical to those in the current commit.

- **Committing Changes (`commit`)**
  - Saves a snapshot of the staged files, creating a new commit.
  - Supports tracking and untracking of files based on staged additions and removals.

- **Removing Files (`rm`)**
  - Unstages and removes files from the working directory.
  - Handles staged files and files tracked by the current commit.

- **Viewing History (`log`, `global-log`)**
  - Displays the commit history starting from the current branch’s head.
  - `log` shows the history of the current branch, while `global-log` shows all commits.

- **Finding Commits (`find`)**
  - Finds and displays all commits that have a given commit message.

- **Branch Management (`branch`, `rm-branch`, `checkout`)**
  - Supports creating new branches, switching between them, and deleting branches.
  - The `checkout` command allows restoring files or switching branches.

## Usage

To use the Gitlet version-control system, compile and run the Java files with the following commands:

```
javac gitlet/Main.java
java gitlet.Main [command] [args]
```

### Example Commands

- **Initialize a Repository**
  ```
  java gitlet.Main init
  ```

- **Add a File to Staging**
  ```
  java gitlet.Main add [file name]
  ```

- **Commit Staged Files**
  ```
  java gitlet.Main commit "commit message"
  ```

- **Remove a File**
  ```
  java gitlet.Main rm [file name]
  ```

- **View Commit History**
  ```
  java gitlet.Main log
  ```

- **View All Commits**
  ```
  java gitlet.Main global-log
  ```

- **Find a Commit by Message**
  ```
  java gitlet.Main find "commit message"
  ```

- **Create a New Branch**
  ```
  java gitlet.Main branch [branch name]
  ```

- **Checkout a Branch**
  ```
  java gitlet.Main checkout [branch name]
  ```

- **Checkout a File from a Specific Commit**
  ```
  java gitlet.Main checkout [commit id] -- [file name]
  ```

## Implementation Details

### Repository Class

- **`CWD`**: Represents the current working directory.
- **`GITLET_DIR`**: The `.gitlet` directory where all version-control data is stored.
- **`BRANCH_DIR`, `ACTIVE_BRANCH`, `MASTER`, `HEAD`**: Manage branches and the HEAD pointer.
- **`COMMIT_DIR`**: Stores all commits.
- **`BLOB_DIR`**: Stores all file versions (blobs).
- **`STAGING_AREA`**: Manages the files staged for addition or removal.

### Commit Class

- Represents a commit object with a message, date, and parent commits.
- Tracks files using a `TreeMap` where the key is the file name and the value is the hash of the file's contents.

### Blob Class

- Handles the creation and storage of blobs (file versions).
- Uses SHA-1 hashes to uniquely identify each blob.

### StagingArea Class

- Manages the files that are staged for addition or removal.
- Provides methods to add files to the staging area, save the state, and clear the staging area after a commit.
