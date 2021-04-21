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
        public $category_name;
        
        public function __construct($db)
        {
            $this->conn = $db;
        }

        //Create an entry
        public function createEntry()
        {
            //make the query
            $query = "INSERT INTO " . $this->db_table ."(start_date_time, end_date_time, note, username, category_name) VALUES (:sdt, :edt, :note, :username, :category_name)";

            //prepare the query
            $stmt = $this->conn->prepare($query);

            //strip characters for XSS and SQLI
            $this->sdt = htmlspecialchars(strip_tags($this->sdt));
            $this->edt = htmlspecialchars(strip_tags($this->edt));
            $this->note = htmlspecialchars(strip_tags($this->note));
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->category_name = htmlspecialchars(strip_tags($this->category_name));

            //bind data to the query
            $stmt->bindParam(":sdt", $this->start_date_time);
            $stmt->bindParam(":edt", $this->end_date_time);
            $stmt->bindParam(":note", $this->note);
            $stmt->bindParam(":username", $this->username);
            $stmt->bindParam(":category_name", $this->category_name);

            //if it successfully executes, return true, else return false
            if($stmt->execute())
            {
                return true;
            }
            return false;
        }

        public function retrieveEntries()
        {
            //create the base query
            $query = "SELECT * FROM " . $this->db_table . " WHERE username = :username";

            //append parts of query based on data passed in
            $query .= isset($this->start_date_time) ? " AND start_date_time >= :sdt" : "";
            $query .= isset($this->end_date_time) ? " AND end_date_time <= :edt" : "";
            $query .= isset($this->note) ? " AND note LIKE :note" : "";
            $query .= isset($this->category_name) ? " AND category_name = :category_name" : "";

            //prepare the query.
            $stmt = $this->conn->prepare($query);

            //strip tags to avoid XSS
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->start_date_time = isset($this->start_date_time) ? htmlspecialchars(strip_tags($this->start_date_time)) : NULL;
            $this->end_date_time = isset($this->end_date_time) ? htmlspecialchars(strip_tags($this->end_date_time)) : NULL;
            $this->note = isset($this->note) ? '%'.htmlspecialchars(strip_tags($this->note)).'%' : NULL;
            $this->category_name = isset($this->category_name) ? htmlspecialchars(strip_tags($this->category_name)) : NULL;

            //bind parameters. Username is always set, but the other variables may not be set.
            //Therefore, check to see if each variable is set. If it is set, bind it.
            $stmt->bindParam(":username", $this->username);
            if(isset($this->start_date_time))
            {
                $stmt->bindParam(":sdt", $this->start_date_time);
            }
            if(isset($this->end_date_time))
            {
                $stmt->bindParam(":edt", $this->end_date_time);
            }
            if(isset($this->note))
            {
                $stmt->bindParam(":note", $this->note);
            }
            if(isset($this->category_name))
            {
                $stmt->bindParam(":category_name", $this->category_name);
            }
            
            //if successful, return the array of returned values. Else, return false.
            if($stmt->execute())
            {
                return $stmt;
            }
            return false;
        }
    }
?>