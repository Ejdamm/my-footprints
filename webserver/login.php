<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$injson = file_get_contents('php://input');
	$injsonarr = json_decode($injson, true);
	$email = $injsonarr['email'];
	$password = $injsonarr['password'];
	$lastId = $injsonarr['lastid'];
	$token = $db->login($email, $password);
	$success = 0;
	$data = "";
	$serverLastId = -1;
	if ($token == "wrongpassword")
		$success = -1;
	else
	{
		$data = $db->pull($email, $lastId);
		$serverLastId = $db->getLastId($email);
		if ($serverLastId == null)
			$serverLastId = 0;
	}
	$outjsonarr = array("success" => $success, "token" => $token, "lastid" => $serverLastId, "data" => $data);
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);
	print_r($outjson);
?>
