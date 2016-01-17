<?php

include ("db_cred.php");
$results  = mysqli_query($db, "SELECT * FROM dac ORDER BY last_used DESC LIMIT 15");
$rowCount = mysqli_num_rows($results);
?>

<html>
<head>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootswatch/3.3.6/united/bootstrap.min.css">
<title>DAC Dashboard</title>
<meta http-equiv="refresh" content="2">
</head>
<div class="col-md-offset-3 col-md-6">
    <h2 class="text-center"> Dumb Arithmetic Calculator </h2>
</div>
<div class="well col-md-offset-3 col-md-6">
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Expression</th>
                <th>Result</th>
                <th>Last Used Timestamp</th>
            </tr>
        </thead>
        <tbody>
<?php
        for ($i=0 ; $i<$rowCount; $i++)
        {
            $row = mysqli_fetch_array($results);
            echo "<tr>
                    <td>".$row['expression']."</td>
                    <td>".$row['result']."</td>
                    <td>".$row['last_used']."</td>
                </tr>";
}
?>
        </tbody>
    </table>
</div>
