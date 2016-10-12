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
		$serverLastId = $db->getLastId($table);
		if ($serverLastId == null)
			$serverLastId = 0;
		$outjsonarr = array("success" => $auth, "lastid" => $serverLastId, "data" => $rows);
	}
	else
	{
		$outjsonarr = array("success" => $auth, "lastid" => -1, "data" => "");
	}
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);
	print_r($outjson);
?>
