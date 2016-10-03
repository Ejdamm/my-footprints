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
			die("Connection failed: " . $conn->connect_error);
		}
 
		return $this->db;
    	}
 
	public function close()
	{
        	if($this->db != null)
			mysqli_close($this->db);
    	}
	
	public function fetchAll()
	{
		$sql = "SELECT * FROM " . DB_TABLE_POSITIONS;
		if (!$result = mysqli_query($this->db, $sql))
		{
			die("Error description: " . mysqli_error($this->db));
		}
		return $result->fetch_all();
	}
}
