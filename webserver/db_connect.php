<?php
DEFINE("TOKENEXPIRATION", (30*24*60*60)); //days, hours, minutes, secconds

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

	public function getLastId($table)
	{
		$sql = "SELECT `id` FROM `$table` ORDER BY id DESC LIMIT 1";
		if (!$query = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		$row = $query->fetch_row();
		$result = null;
		if ($row != null)
			$result = $row[0];
		return $result;	
	}
	
	public function authenticate($email, $token)
	{
		$hashedToken = md5($token);
		$sql = "SELECT `expire` FROM users WHERE email = '$email' AND token = '$hashedToken'";
		if(!$result = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		$retVal = $result->num_rows - 1; //0 if ok and -1 if wrong token/email 
		if($retVal > -1)
		{
			$row = $result->fetch_row();
			if($row[0] < time())
				$retVal = -2; //token has expired
		}
		return $retVal;
	}

	public function login($email, $password)
	{
		$hashedPassword = md5($password);
		$sql = "SELECT `id` FROM users WHERE email = '$email' AND password = '$hashedPassword'";
		if (!$result = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		
		if($result->num_rows > 0)	
		{
			$row = $result->fetch_row();	
			$index = $row[0];
			$token = bin2hex(random_bytes(128));
			$hashedToken = md5($token);
			$expire = time() + TOKENEXPIRATION;
			$sql = "UPDATE `users` SET `token` = '$hashedToken', `expire` = $expire WHERE `id` = $index";
			if (!$result = $this->db->query($sql))
			{
				die("Error description: " . $this->db->error);
			}
		}
		else
			$token = "wrongpassword";
		return $token;
	}

	
	public function pull($table, $lastId)
	{
		$sql = "SELECT * FROM `$table` WHERE `id` > $lastId";
		if (!$result = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		return $result->fetch_all(MYSQLI_ASSOC);
	}

	public function push($table, $arr)
	{
		$columns =  "`id`, `session`, `accessedTimestamp`, `latitude`, `longitude`";
		foreach($arr as $row)
		{
			$id = $row['id'];
			$session = $row['session'];
			$accessedTimestamp = $row['accessedTimestamp'];
			$latitude = $row['latitude'];
			$longitude = $row['longitude'];
			$values = "$id, $session, $accessedTimestamp, $latitude, $longitude";	
			$sql = "INSERT INTO `$table` ($columns) VALUES ($values)";
			if (!$this->db->query($sql))
			{
				die("Error description: " . $this->db->error);
                	}
		}
	}
	
	public function createUser($email, $password)
	{
		$sql = "SELECT `id` FROM users WHERE email = '$email'";
		if (!$result = $this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
		}
		if ($result->num_rows == 0)
		{
			$hashedPassword = md5($password);
			$token = bin2hex(random_bytes(128));
			$hashedToken = md5($token);
			$expire = time() + TOKENEXPIRATION;
			$columns = "`email`, `password`, `token`, `expire`";
			$values = "'$email', '$hashedPassword', '$hashedToken', $expire";
			$sql = "INSERT INTO users ($columns) VALUES ($values)";
			if (!$this->db->query($sql))
			{
				die("Error description: " . $this->db->error);
               		}
			$this->createIndividualTable($email);
		}
		else
			$token = "alreadyexists";
		return $token;
	}

	
	public function createIndividualTable($table)
	{
		$sql = "CREATE TABLE IF NOT EXISTS `$table` (
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

	public function createUsersTable()
	{
		$sql = " CREATE TABLE `users` (
			`id` int(11) NOT NULL AUTO_INCREMENT,
	  		`email` varchar(320) NOT NULL,
	  		`password` varchar(256) NOT NULL,
	  		`token` varchar(256) NOT NULL,
	  		`expire` int(11) NOT NULL,
			PRIMARY KEY (`id`)
			)";
		if (!$this->db->query($sql))
		{
			die("Error description: " . $this->db->error);
               	}
				
	}
}
