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

        //retrieve entries based on values passed in
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

        public function retrieveOverview()
        {
            //create the base query
            $query = "
            SELECT e1.username, 
            e1.category_name, 
            SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS category_time, 
            (SUM(TIME_TO_SEC(timediff(e1.end_date_time, e1.start_date_time)))/e2.total_time * 100) AS percent_time 
            FROM entry e1 
            INNER JOIN
            (
                SELECT e2.username, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) as total_time
                FROM entry e2
                GROUP BY username
            ) AS e2
            ON e1.username = e2.username
            WHERE e1.username = :username
            GROUP BY e1.category_name";

            //prepare the query.
            $stmt = $this->conn->prepare($query);

            //strip tags to avoid XSS
            $this->username = htmlspecialchars(strip_tags($this->username));

            //bind username paramater
            $stmt->bindParam(":username", $this->username);
            
            //if successful, return the array of returned values. Else, return false.
            if($stmt->execute())
            {
                return $stmt;
            }
            return false;
        }

        public function retrieveComparison()
        {

            //Create an array of question marks to be placeholders for each category passed in
            $categoryPlaceHolders = isset($this->category_name) ? str_repeat('?,', count($this->category_name)-1) . '?' : NULL;

            //Create an array to be filled with strings to replace the placeholders
            $queryVariables = array();

            //strip tags to avoid XSS
            $this->username = htmlspecialchars(strip_tags($this->username));
            $this->start_date_time = isset($this->start_date_time) ? htmlspecialchars(strip_tags($this->start_date_time)) : NULL;
            $this->end_date_time = isset($this->end_date_time) ? htmlspecialchars(strip_tags($this->end_date_time)) : NULL;
            $this->note = isset($this->note) ? '%' . htmlspecialchars(strip_tags($this->note)) . '%': NULL;
            
            //create the base query
            $query = "
            SELECT
            e1.username,
            e1.category_name,
            SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS category_time,
            SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time)))/e2.total_time * 100 AS percent_time,
            global_category_time,
            global_category_time/global_total_time * 100 AS global_percent_time
            FROM entry e1
            INNER JOIN(
            SELECT e2.username, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS total_time
            FROM entry e2
            WHERE e2.username = ?";
            
            //append parts of query based on data passed in and push them onto the variable array
            array_push($queryVariables, $this->username);
            if(isset($this->start_date_time))
            {
                $query .= " AND e2.start_date_time >= ?";
                array_push($queryVariables, $this->start_date_time);
            }

            if(isset($this->end_date_time))
            {
                $query .= " AND e2.end_date_time <= ?";
                array_push($queryVariables, $this->end_date_time);
            }

            if(isset($this->note))
            {
                $query .= " AND e2.note LIKE ?";
                array_push($queryVariables, $this->note);
            }

            if(isset($this->category_name))
            {
                $query .= " AND e2.category_name IN ($categoryPlaceHolders)";
                $queryVariables = array_merge($queryVariables, $this->category_name);
            }

            $query .= "
            GROUP BY e2.username
            ) AS e2
            ON e1.username = e2.username
            INNER JOIN(
            SELECT e3.category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS global_category_time
            FROM entry e3";
            
            //append WHERE clause if any data is set
            if((isset($this->start_date_time) || isset($this->end_date_time) || isset($this->category_name)))
            {
                $query .= " WHERE";
            }

            //append parts of query based on data passed in
            if(isset($this->start_date_time))
            {
                $query .= " e3.start_date_time >= ?";
                array_push($queryVariables, $this->start_date_time);
            }

            //if both the start_date_time and the end_date_time are set, add the AND clause, otherwise don't
            if(isset($this->end_date_time, $this->start_date_time))
            {
                $query .= " AND e3.end_date_time <= ?";
                array_push($queryVariables, $this->end_date_time);
            } else if (isset($this->end_date_time) && !isset($this->start_date_time))
            {
                $query .= " e3.end_date_time <= ?";
                array_push($queryVariables, $this->end_date_time);
            }

            if(isset($this->category_name) && (isset($this->start_date_time) || isset($this->end_date_time)))
            {
                $query .= " AND e3.category_name IN ($categoryPlaceHolders)";
                $queryVariables = array_merge($queryVariables, $this->category_name);
            } else if(isset($this->category_name))
            {
                $query .= " e3.category_name IN ($categoryPlaceHolders)";
                $queryVariables = array_merge($queryVariables, $this->category_name);
            }
            
            $query .= "
            GROUP BY e3.category_name
            ) AS e3
            ON e1.category_name = e3.category_name
            INNER JOIN(
            SELECT SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS global_total_time
            FROM entry e4";
            
            //append WHERE clause if any data is set
            if(isset($this->start_date_time) || isset($this->end_date_time) || isset($this->category_name))
            {
                $query .= " WHERE";
            }

            //append parts of query based on data passed in
            if(isset($this->start_date_time))
            {
                $query .= " e4.start_date_time >= ?";
                array_push($queryVariables, $this->start_date_time);
            }

            //if both the start_date_time and the end_date_time are set, add the AND clause, otherwise don't
            if(isset($this->end_date_time, $this->start_date_time))
            {
                $query .= " AND e4.end_date_time <= ?";
                array_push($queryVariables, $this->end_date_time);
            } else if (isset($this->end_date_time) && !isset($this->start_date_time))
            {
                $query .= " e4.end_date_time <= ?";
                array_push($queryVariables, $this->end_date_time);
            }

            if(isset($this->category_name) && (isset($this->start_date_time) || isset($this->end_date_time)))
            {
                $query .= " AND e4.category_name IN ($categoryPlaceHolders)";
                $queryVariables = array_merge($queryVariables, $this->category_name);
            } else if(isset($this->category_name))
            {
                $query .= " e4.category_name IN ($categoryPlaceHolders)";
                $queryVariables = array_merge($queryVariables, $this->category_name);
            }
            
            $query .= ") AS e4
            WHERE e1.username = ?";

            //append parts of query based on data passed in
            $query .= isset($this->start_date_time) ? " AND e1.start_date_time >= ?" : "";
            $query .= isset($this->end_date_time) ? " AND e1.end_date_time <= ?" : "";
            $query .= isset($this->note) ? " AND e1.note LIKE ?" : "";
            $query .= isset($this->category_name) ? " AND e1.category_name IN ($categoryPlaceHolders)" : "";
            
            //append the GROUP BY clause at the end
            $query .= " GROUP BY e1.username, e1.category_name, global_total_time";
            
            //push variables into the array for this last part of the query
            array_push($queryVariables, $this->username);
            if(isset($this->start_date_time))
            {
                array_push($queryVariables, $this->start_date_time);
            }
            if(isset($this->end_date_time))
            {
                array_push($queryVariables, $this->end_date_time);
            }
            if(isset($this->note))
            {
                array_push($queryVariables, $this->note);
            }
            if(isset($this->category_name))
            {
                $queryVariables = array_merge($queryVariables, $this->category_name);
            }

            //prepare the query.
            $stmt = $this->conn->prepare($query);

            //if successful, return the array of returned values. Else, return false.
            if($stmt->execute($queryVariables))
            {
                return $stmt;
            }
            return false;
        }

        //delete entry based on entry_id
        public function deleteEntry()
        {
            //write the delete query
            $query = "DELETE FROM " . $this->db_table . " WHERE entry_id = :entry_id";

            //prepare the query for binding and execution
            $stmt = $this->conn->prepare($query);

            //strip tags to prevent XSS
            $this->entry_id = htmlspecialchars((strip_tags($this->entry_id)));

            //bind values
            $stmt->bindParam(":entry_id", $this->entry_id);

            //execute the statement
            if($stmt->execute())
            {
                //if a row was actually deleted, return true
                if($stmt->rowCount() > 0)
                {
                    return true;
                }
            }
            return false;
        }

        public function updateEntry()
        {
            
            //set up the query...
            $query = "UPDATE " . $this->db_table . " SET";
            
            //append parts of query based on data passed in
            $query .= isset($this->start_date_time) ? " start_date_time = :sdt," : "";
            $query .= isset($this->end_date_time) ? " end_date_time = :edt," : "";
            $query .= isset($this->note) ? " note = :note," : "";
            $query .= isset($this->category_name) ? " category_name = :category_name" : "";

            //If the last character is a comma, delete it. Otherwise, leave the query string alone.
            $query = (mb_substr($query, -1) === ',') ? mb_substr($query, 0 ,-1) : $query;
            
            //append WHERE clause
            $query .= " WHERE entry_id = :entry_id";

            //prepare query for binding and executing
            $stmt = $this->conn->prepare($query);
            
            //strip tags to avoid XSS
            $this->entry_id = isset($this->entry_id) ? htmlspecialchars(strip_tags($this->entry_id)) : NULL;
            $this->start_date_time = isset($this->start_date_time) ? htmlspecialchars(strip_tags($this->start_date_time)) : NULL;
            $this->end_date_time = isset($this->end_date_time) ? htmlspecialchars(strip_tags($this->end_date_time)) : NULL;
            $this->note = isset($this->note) ? htmlspecialchars(strip_tags($this->note)) : NULL;
            $this->category_name = isset($this->category_name) ? htmlspecialchars(strip_tags($this->category_name)) : NULL;
            
            //bind parameters. entry id is always set, but the other variables may not be set.
            //Therefore, check to see if each variable is set. If it is set, bind it.
            $stmt->bindParam(":entry_id", $this->entry_id);
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
            
            //if successful, return true else return false
            if($stmt->execute())
            {
                //if a row was actually updated, return true
                if($stmt->rowCount() > 0)
                {
                    return true;
                }
            }
            return false;
        }
    }
?>