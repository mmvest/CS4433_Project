<?php
    class Category
    {
        //connection variable
        private $conn;

        //table name
        private $db_table = "category";

        //table columns
        public $id;
        public $name;
        
        public function __construct($db)
        {
            $this->conn = $db;
        }

        public function retrieveCategories()
        {
            $query = "SELECT * FROM " . $this->db_table;

            $stmt = $this->conn->prepare($query);

            //if successful, return the array of returned values. Else, return false.
            if($stmt->execute())
            {
                return $stmt;
            }
            return false;
        }
    }
?>