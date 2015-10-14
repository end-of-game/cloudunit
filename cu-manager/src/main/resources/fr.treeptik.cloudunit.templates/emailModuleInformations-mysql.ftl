<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>*|MC:SUBJECT|*</title>
</head>

<body style="margin: 0; padding: 0; background-color: #128abc" bgcolor="#128abc">

<table cellpadding="0" cellspacing="0" border="0" align="center" width="100%">
    <tbody>
    <tr>
        <td align="center" style="padding: 37px 0; background-color: #128abc;" bgcolor="#128abc">

            <!-- #nl_container -->
            <table cellpadding="0" cellspacing="0" border="0"
                   style="margin: 0; color: #505c64; font-family: arial; font-size: 14px; background-color: #ffffff; "
                   width="600">
                <tbody>
                <tr>
                    <td>

                        <!-- #nl_header -->
                        <table cellpadding="0" cellspacing="0" border="0" width="100%"
                               style="background-color: #ededed; padding: 24px">
                            <tbody>
                            <tr>
                                <td style="text-align: left; color: #505c64; font-weight: bold; font-size: 18px;">
                                    <img src="http://treeptik.fr/medias/logo-cloudunit.png" height="24px"
                                         alt="Logo de CloudUnit"> CloudUnit
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <!-- #nl_content -->
                        <table cellpadding="0" cellspacing="0" border="0"
                               style="margin: 0; border-collapse: collapse; font-size: 14px"
                               width="100%">
                            <tbody>
                            <tr>
                                <td style="color: #505c64; font-family: arial; border-color: #ffffff; background-color: #ffffff;  padding: 5px 0;"
                                    align="left">
                                    <table style="margin: 0 0 0 10px;border-collapse:collapse;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">
                                        <tbody>
                                        <tr>
                                            <td width="580" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="border-collapse:collapse;width:565px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="565">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px;line-height:normal;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">

                                                            <h2 style="font-size: 2em;margin: 24px;text-align: center;">
                                                                MySQL database is installed</h2>

                                                            <p>Hello <strong>${userLogin}</strong>, you asked about an
                                                                database installation.</p>

                                                            <p>You will find there the informations to configure your
                                                                dataSource</p>

                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <table style="margin: 0 0 0 10px;border-collapse:collapse;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">
                                        <tbody>
                                        <tr>
                                            <td width="580" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="border-collapse:collapse;width:565px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="565">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px; line-height:normal;">
                                                            <hr style="margin: 0;display: block; height: 1px; line-height: 0; width: 100%; border: none; background-color: #D1D1D1; ">
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <table style="margin: 0 0 0 10px;border-collapse:collapse;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">
                                        <tbody>
                                        <tr>
                                            <td width="580" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="border-collapse:collapse;width:565px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="565">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px;line-height:normal;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">

                                                            <h4 style="text-align: center">Informations about your
                                                                database</h4>
                                                            <ul style="list-style: none">
                                                                <li><strong>Database name: </strong>
                                                                ${mysqlDatabase}
                                                                </li>
                                                                <li><strong>Your MySQL user: </strong>
                                                                ${mysqlUser}
                                                                </li>
                                                                <li><strong>Your MySQL password: </strong>
                                                                ${mysqlPassword}
                                                                </li>
                                                                <li><strong>Domaine name: </strong>
                                                                ${internalDNSName}
                                                                </li>
                                                                <li><strong>Mysql port: </strong>
                                                                    3306
                                                                </li>
                                                                <li><strong>Complete link to access your
                                                                    database:</strong>
                                                                    jdbc:mysql://${internalDNSName}
                                                                    :3306/${mysqlDatabase}
                                                                </li>
                                                            </ul>

                                                            <h4 style="text-align: center">You can also use these
                                                                environment variables to connect to your database</h4>
                                                            <ul style="list-style: none">
                                                                <li><strong>Database name: </strong>
                                                                ${r"${CU_DATABASE_NAME}"}
                                                                </li>
                                                                <li><strong>Your MySQL MySQL: </strong>
                                                                ${r"${CU_DATABASE_USER_MYSQL_"}${module_seq}}
                                                                </li>
                                                                <li><strong>Your password MySQL: </strong>
                                                                ${r"${CU_DATABASE_PASSWORD_MYSQL_"}${module_seq}}
                                                                </li>
                                                                <li><strong>Domaine name: </strong>
                                                                ${r"${CU_DATABASE_DNS_MYSQL_"}${module_seq}}
                                                                </li>
                                                                <li><strong>Mysql port: </strong>
                                                                    3306
                                                                </li>
                                                                <li><strong>Complete link to access your
                                                                    database:</strong>
                                                                    jdbc:mysql://${r"${CU_DATABASE_DNS_MYSQL_"}${module_seq}
                                                                    }:3306/${r"${CU_DATABASE_NAME}"}
                                                                </li>

                                                            </ul>


                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <table style="margin: 0 0 0 10px;border-collapse:collapse;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">
                                        <tbody>
                                        <tr>
                                            <td width="580" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="border-collapse:collapse;width:565px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="565">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px; line-height:normal;">
                                                            <hr style="margin: 0;display: block; height: 1px; line-height: 0; width: 100%; border: none; background-color: #D1D1D1; ">
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>

                                    <table style="margin: 0 0 0 10px;border-collapse:collapse;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">
                                        <tbody>
                                        <tr>
                                            <!--<td width="290" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="border-collapse:collapse;width:275px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="275">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px;line-height:normal;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>-->
                                            <td width="290" style="vertical-align: top; padding: 5px 0; ">
                                                <table cellpadding="0" cellspacing="0"
                                                       style="margin-bottom: 16px; border-collapse:collapse;width:275px;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; "
                                                       width="275">
                                                    <tbody>
                                                    <tr>
                                                        <td style="padding:5px 0 5px 5px;line-height:normal;color: #505c64; font-family: arial; font-size: 14px; border-color: #ffffff; background-color: #ffffff; ">


                                                            <h4 style="font-size: 14px;margin: 0 0 10px;">Stay
                                                                tuned:</h4>
                                                            <!--<hr style="background-color: rgb(80, 92, 100);">-->
                                                            <table style="height: 15px; width: 258px;"
                                                                   class="cke_show_border" cellpadding="8">
                                                                <tbody>
                                                                <tr>
                                                                    <td style="width: 258px; text-align: left;">
                                                                        <a href="https://twitter.com/treeptikTeam"
                                                                           style="color: #ffffff; ;border: none; margin-right: 4px;"><img
                                                                                src="http://treeptik.fr/medias/icon-twitter.jpg"
                                                                                height="24"></a>
                                                                        <a href="https://plus.google.com/101644017807219815482"
                                                                           style="color: #ffffff; ;border: none;"><img
                                                                                src="http://treeptik.fr/medias/icon-google.jpg"
                                                                                height="24"></a>
                                                                    </td>
                                                                </tr>
                                                                </tbody>
                                                            </table>

                                                            <br><h4 style="font-size: 14px;margin: 0 0 10px;">
                                                            Support:</h4>
                                                            <!--<hr style="background-color: rgb(80, 92, 100);">-->
                                                            Email: <a
                                                                href="support@cloudunit.fr"
                                                                style="color: #ffffff; ;border: none;"><span
                                                                style="color:#00ccff;">support@cloudunit.fr</span></a><br>
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                </tbody>
            </table>
            <table cellpadding="0" cellspacing="0" border="0" height="10"
                   style="height: 10px;border-collapse: collapse;font-size: 1px;">
                <tbody>
                <tr>
                    <td height="10" style="height:10px; border-spacing: 0;font-size: 1px;">&nbsp;</td>
                </tr>
                </tbody>
            </table>

        </td>
    </tr>
    </tbody>
</table>


</body>
</html>