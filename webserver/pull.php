<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$injson = file_get_contents('php://input');
	$injsonarr = json_decode($injson, true);
	$table = $injsonarr['email'];
	$token = $injsonarr['token'];
	$lastId = $injsonarr['lastid']; 
	$auth = $db->authenticate($table, $token);
	if ($auth == 0)
	{
		$rows = $db->pull($table, $lastId);
		$outjsonarr = array("success" => $auth, "data" => $rows);
	}
	else
	{
		$outjsonarr = array("success" => $auth, "data" => "");
	}
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);
	print_r($outjson);
?>
