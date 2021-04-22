<?php
    class User
    {
        //connection
        private $conn;

        //table
        private $db_table = "user";

        //columns
        public $username;
        public $salt;
        public $password;
        public $created;

        // Connect to db when created
        public function __construct($db)
        {
            $this->conn = $db;
        }

        // Create User
        public function createUser()
        {
            $sqlQuery = 'INSERT INTO ' . $this->db_table .'
                SET username = :username, 
                    salt = :salt, 
                    password = :password, 
                    created = :created';

            //uncomment this for debugging
            //$this->conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );

            $stmt = $this->conn->prepare($sqlQuery);

            // strip characters to prevent SQLI
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->password = htmlspecialchars(strip_tags($this->password));
            $this->created = htmlspecialchars(strip_tags($this->created));
            
            // Set salt - generate random 64 character long salt
            $this->salt = bin2hex(random_bytes(32));

            //Prepend salt to password and hash password
            $this->password = $this->salt . $this->password;
            $this->password = hash('sha256', $this->password);

            //bind data
            $stmt->bindParam(":username", $this->username);
            $stmt->bindParam(":salt", $this->salt);
            $stmt->bindParam(":password", $this->password);
            $stmt->bindParam(":created", $this->created);

            if($stmt->execute())
            {
                return true;
            }
            //uncomment this for debugging
            //print_r($stmt->errorInfo());
            return false;
        }

        // Login User
        public function loginUser()
        {
            $sqlQuery = 'SELECT * FROM ' . $this->db_table .'
                WHERE username = :username'; 

            //uncomment this for debugging
            //$this->conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );

            $stmt = $this->conn->prepare($sqlQuery);

            // strip characters to prevent XSS
            $this->username = htmlspecialchars(strip_tags($this->username));

             //bind data
             $stmt->bindParam(":username", $this->username);

            //We are using this to check if the account even exists before doing comparisons
            if(!($stmt->execute()))
            {
                //uncomment this for debugging
                //print_r($stmt->errorInfo());
                exit('Error logging in.');
            }

            //If the account exists
            if (($data = $stmt->fetch(PDO::FETCH_ASSOC)) !== false) {

                // strip characters to prevent SQLI
                $inputPassword = htmlspecialchars(strip_tags($this->password));

                $this->username = $data['username'];
                $this->password = $data['password'];
                $this->salt = $data['salt'];
                
                //hash the input password to prepare it for comparison
                $inputPassword = hash('sha256', $this->salt . $inputPassword);

                //if the input password is completely identical to the password in the DB then...
                if( $inputPassword === $this->password) {
                    // create session data
                    session_regenerate_id();
                    $_SESSION['loggedin'] = TRUE;
                    $_SESSION['user'] = $this->username;
                    return true;
                } else {
                    // Incorrect password
                    echo 'Incorrect username or password. Please try again.';
                    return false;
                }
            } else {
                // Incorrect username
                echo 'Account does not exist or you used an incorrect username and password. Please try again.';
                return false;
            }

            $stmt->close();
        }

        public function updatePassword()
        {
            $query = "UPDATE " . $this->db_table . " SET salt = :salt, password = :password WHERE username = :username";

            $stmt = $this->conn->prepare($query);

            //strip tags to avoid XSS
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->password = htmlspecialchars(strip_tags($this->password));

            // Set salt - generate random 64 character long salt
            $this->salt = bin2hex(random_bytes(32));

            //Prepend salt to password and hash password
            $this->password = $this->salt . $this->password;
            $this->password = hash('sha256', $this->password);

            //bind variables to prepared parameters
            $stmt->bindParam(":salt", $this->salt);
            $stmt->bindParam(":password", $this->password);
            $stmt->bindParam("username", $this->username);

            if($stmt->execute())
            {
                return true;
            }
            return false;
        }

        public function deleteUser()
        {
            //Check if the password given is correct first. Do this by getting the user information
            //and hashing the password with the salt and then comparing...
            $sqlQuery = 'SELECT salt, password FROM ' . $this->db_table .' WHERE username = :username'; 

            $stmt = $this->conn->prepare($sqlQuery);

            // strip characters to prevent XSS
            $this->username = htmlspecialchars(strip_tags($this->username));

            //bind data
            $stmt->bindParam(":username", $this->username);

            //We are using this to check if the account even exists before doing comparisons
            if(!($stmt->execute()))
            {
                echo 'Error Deleting Account. Account does not exist';
                return false;
            }

            //If the account exists
            if (($rowData = $stmt->fetch(PDO::FETCH_ASSOC)) !== false)
            {

                // strip characters to prevent SQLI
                $inputPassword = htmlspecialchars(strip_tags($this->password));

                $this->password = $rowData['password'];
                $this->salt = $rowData['salt'];
                
                //hash the input password to prepare it for comparison
                $inputPassword = hash('sha256', $this->salt . $inputPassword);

                //if the input password is completely identical to the password in the DB then...
                if( $inputPassword === $this->password)
                {
                    //delete the account
                    $sqlQuery = "DELETE FROM " . $this->db_table . " WHERE username = :username AND password = :password";
                    $stmt = $this->conn->prepare($sqlQuery);
                    $this->username = htmlspecialchars(strip_tags($this->username));
                    $stmt->bindParam(":username", $this->username);
                    $stmt->bindParam(":password", $this->password);

                    if($stmt->execute())
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }