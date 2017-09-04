<?php
    
    error_reporting( E_ALL );

    function getUserIP()
    {
        $client  = @$_SERVER['HTTP_CLIENT_IP'];
        $forward = @$_SERVER['HTTP_X_FORWARDED_FOR'];
        $remote  = $_SERVER['REMOTE_ADDR'];

        if(filter_var($client, FILTER_VALIDATE_IP))
        {
            $ip = $client;
        }
        elseif(filter_var($forward, FILTER_VALIDATE_IP))
        {
            $ip = $forward;
        }
        else
        {
            $ip = $remote;
        }

        return $ip;
    }    
    
    
    $temp_file = 'ip.txt';
    $identity = isset($_GET['ip']) ? $_GET['ip'] : '';
        
    // Open the file to get existing content
    $current = file_get_contents($temp_file);

    if ($identity!='')
    {           
        $current = getUserIP();
        // Write the contents to the file
        file_put_contents($temp_file, $current);
    }
    echo $current;
?>