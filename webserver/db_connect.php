<?php
class DB_CONNECT {
	private $db = null;
 
	public function __construct()
	{
		$this->connect();
	}
 
	public function __destruct()
	{
        	$this->close();
    	}
 
   	public function connect()
	{
       		require_once __DIR__ . '/db_config.php';
	
		$this->db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
	
		if ($this->db->connect_error)
		{
			die("Connection failed: " . $this->db->connect_error);
		}
 
		return $this->db;
    	}
 
	public function close()
	{
        	if($this->db != null)
			$this->db->close();	
    	}

	public function createTable($table)
	{
		$sql = "CREATE TABLE IF NOT EXISTS $table (
  			`id` int(11) NOT NULL,
  			`session` int(11) DEFAULT NULL,
  			`accessedTimestamp` int(11) DEFAULT NULL,
 			`latitude` double DEFAULT NULL,
  			`longitude` double DEFAULT NULL,
			PRIMARY KEY (`id`)
			)";
		if (!$this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
	}
	
	public function pull($table, $lastId)
	{
		$this->createTable($table);
		$sql = "SELECT * FROM $table WHERE `id` > $lastId";
		if (!$result = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		return $result->fetch_all();
	}

	public function push($table, $arr)
	{
		$columns =  "id, session, accessedTimestamp, latitude, longitude";
		foreach($arr as $row)
		{
			$id = $row['id'];
			$session = $row['session'];
			$accessedTimestamp = $row['accessedTimestamp'];
			$latitude = $row['latitude'];
			$longitude = $row['longitude'];
			$values = "$id, $session, $accessedTimestamp, $latitude, $longitude";	
			$sql = "INSERT INTO $table ($columns) VALUES ($values)";
			if (!$this->db->query($sql))
			{
				die("Error description: " . $this->db->error);
                	}
		}
	}

}
