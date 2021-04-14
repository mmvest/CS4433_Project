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
            //Check if username and password are in the POST request
            if(!isset($_POST['username'], $_POST['password']))
            {
                //If we can't get the data, return error
                exit('We need both a username and password to login silly goose.');
            }

            $this->username = $_POST['username'];
            $inputPassword = $_POST['password'];


            //Now prepare our login in statements
            $sqlQuery = 'SELECT username, salt, password FROM ' . $this->db_tableuser . ' WHERE username = :username';
            $stmt = $this->conn->prepare($sqlQuery);

            // strip characters to prevent SQLI
            $this->username = htmlspecialchars(strip_tags($this->username));
            $inputPassword = htmlspecialchars(strip_tags($inputPassword));
            
            //bind the parameter
            $stmt->bindParam(":username", $this->username);

            //We are using this to check if the account even exists before doing comparisons
            $stmt->execute();
            $stmt->store_result();

            //If the account exists
            if ($stmt->num_rows > 0) {
                //prepare these variables to be bound
                $stmt->bind_result($this->username, $this->salt, $this->password);
                
                //fetch results from prepared statement and put them into the bound variables
                $stmt->fetch();
                
                //hash the input password to prepare it for comparison
                $inputPassword = hash('sha256', $this->salt . $inputPassword);

                //if the input password is completely identical to the password in the DB then...
                if( $inputPassword === $this->password) {
                    // create session data
                    session_regenerate_id();
                    $_SESSION['loggedin'] = TRUE;
                    $_SESSION['user'] = $_POST['username'];
                    return true;
                } else {
                    // Incorrect password
                    echo 'Incorrect username or password. Please try again.';
                    return false
                }
            } else {
                // Incorrect username
                echo 'Incorrect username or password. Please try again.';
                return false;
            }

            $stmt->close();
        }
    }