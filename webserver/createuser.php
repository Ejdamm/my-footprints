<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$injson = file_get_contents('php://input');
	$injsonarr = json_decode($injson, true);
	$email = $injsonarr['email'];
	$password = $injsonarr['password'];
	if ($email != "")
		$token = $db->createUser($email, $password);
	else
		$token = "emptyemail";
	$outjsonarr = array("token" => $token);
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);
	print_r($outjson);
?>
