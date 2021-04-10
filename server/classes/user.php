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

            //for debugging
            $this->conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING );

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
            if(!$stmt->bindParam(":username", $this->username))
            {
                echo 'username binding failed';
            }
            if(!$stmt->bindParam(":salt", $this->salt))
            {
                echo 'salt binding failed';
            }
            if(!$stmt->bindParam(":password", $this->password))
            {
                echo 'pass binding failed';
            }
            if(!$stmt->bindParam(":created", $this->created))
            {
                echo 'created binding failed';
            }

            echo 'executing\n';
            if($stmt->execute())
            {
                return true;
            }
            print_r($stmt->errorInfo());
            return false;
        }

        // Get User for debugging purposes
        public function getUser()
        {
            $sqlQuery = "SELECT username, salt, password, created FROM". $this->db_table ." WHERE username = ? LIMIT 0,1";

            $stmt = $this->conn->prepare($sqlQuery);

            $stmt->bindParam(1, $this->id);

            $stmt->execute();

            $dataRow = $stmt->fetch(PDO::FETCH_ASSOC);
            
            $this->username = $dataRow['username'];
            $this->salt = $dataRow['salt'];
            $this->password = $dataRow['password'];
            $this->created = $dataRow['created'];
        }
    }