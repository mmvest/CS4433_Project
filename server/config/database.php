<?php
    class Database
    {
        private $host = '127.0.0.1';
        private $database_name = 'test_timekeeper';
        private $username = 'apiuser';
        private $password = 'apiUSERp@55w0rd';

        public $conn;

        public function getConnection()
        {
            $this->conn = null;
            try
            {
                $this->conn = new PDO("mysql:dbname=" . $this->database_name . ";host=" . $this->host, $this->username, $this->password);
                $this->conn->exec("set names utf8");
            }catch(PDOException $exception){
                echo "Database could not be connected: " . $exception->getMessage();
            }
            return $this->conn;
        }
    }
?>