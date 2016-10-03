<?php
	include 'db_connect.php';
	echo "Welcome, I am connecting Android to PHP, MySQL<br>";
	$db = new DB_CONNECT();

	print_r($db->fetchAll());
?>
