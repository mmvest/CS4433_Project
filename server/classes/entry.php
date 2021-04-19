<?php
    class Entry
    {
        //connection variable
        private $conn;

        //table name
        private $db_table = "entry";

        //table columns
        public $entry_id;
        public $start_date_time;
        public $end_date_time;
        public $note;
        public $username;
        public $category_id;
        public $category_name;
        
        public function __construct($db)
        {
            $this->conn = $db;
        }

        //Create an entry
        public function createEntry()
        {
            //initialize variable to fetch and store requests
            $data = NULL;

            //First, make a query to get the category_id based on category_name
            $category_db = "category";
            $id_query = "SELECT id FROM " . $category_db . " WHERE name = :category_name";
            $id_stmt = $this->conn->prepare($id_query);
            $this->category_name = htmlspecialchars((strip_tags($this->category_name)));
            $id_stmt->bindParam(":category_name", $this->category_name);
            
            //execute the query. On success, fetch the response and store the variables. On failure, return false.
            if($id_stmt->execute())
            {
                $data = $id_stmt->fetch(PDO::FETCH_ASSOC);

                //get the category_id
                $this->category_id = $data['id'];
            } else 
            {
                return false;
            }

            //make the query
            $query = "INSERT INTO " . $this->db_table ."(start_date_time, end_date_time, note, username, category_id) VALUES (:sdt, :edt, :note, :username, :category_id)";

            //prepare the query
            $stmt = $this->conn->prepare($query);

            //strip characters for XSS and SQLI
            $this->sdt = htmlspecialchars(strip_tags($this->sdt));
            $this->edt = htmlspecialchars(strip_tags($this->edt));
            $this->note = htmlspecialchars(strip_tags($this->note));
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->category_id = htmlspecialchars(strip_tags($this->category_id));

            //bind data to the query
            $stmt->bindParam(":sdt", $this->start_date_time);
            $stmt->bindParam(":edt", $this->end_date_time);
            $stmt->bindParam(":note", $this->note);
            $stmt->bindParam(":username", $this->username);
            $stmt->bindParam(":category_id", $this->category_id);

            //if it successfully executes, return true, else return false
            if($stmt->execute())
            {
                return true;
            }

            return false;
        }
    }
?>