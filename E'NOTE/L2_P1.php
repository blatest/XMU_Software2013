<!DOCTYPE HTML>
<html>
	<head>
		<title>Welcome to Easy Note</title>
	</head>
	<body>
	<?php 
		//$judge��ʼֵΪ0������1������Ŷ����ѱ�ע��,����2��ʾע��ɹ�������3��ʾ���ݿ����,����4����ע��ʱ�Ŷ������������,����5��ʾ����ע��ʱ�ŶӲ�����
		include 'data.php';
		$judge=0;
		$teamncs = mysql_query("SELECT name,code,id FROM teams");
		
		while($teamnc = mysql_fetch_array($teamncs))
		{
			if( $teamnc["name"]==$_POST["L2_T2_T"] )
			{
				//�ŶӴ���
				$judge = 1;
				if( $_POST['L2_T2_H']==1 )
				{
					//���û���ע���Ŷ�
					echo "<script>
							window.parent.document.getElementById('L2_T2_T').value='';
							window.parent.document.getElementById('L2_T2_T').placeholder='The team is existing';
							window.parent.document.getElementById('L2_T2_T').style.backgroundColor='RGB(55,96,146)';
						 </script>";
					
				}
				else
				{
					//�û���ע����Ŷӵĳ�Ա
					if( $teamnc["code"]==$_POST["L2_T2_C1"] )
					{
						//�Ŷ�������ȷ
						$table= "t".$teamnc["id"];
						
						$quser = mysql_query("SELECT * FROM ".$table." where name = '".$_POST["L2_T2_U"]."'");
						if($user = mysql_fetch_array($quser))		
						{
							//��Ա����
								echo "<script>
										window.parent.document.getElementById('L2_T2_U').value='';
										window.parent.document.getElementById('L2_T2_U').placeholder='The user is existing';
										window.parent.document.getElementById('L2_T2_U').style.backgroundColor='RGB(55,96,146)';
									  </script>";	
						}
						else
						{
							//�����Ա��
							$insert= "INSERT INTO ".$table."(name,code,email,realname,telephone,address)VALUES( '$_POST[L2_T2_U]', '$_POST[L2_T2_C3]', '$_POST[L2_T2_M]','0','0','0' )";
							if( mysql_query($insert,$con) )
							{
								//ע��ɹ�
								$judge= 2;
								echo "<script>
										window.parent.L2_F1();
										window.parent.document.getElementById('L1_T5').style.display='block';
										setTimeout('".'window.parent.document.getElementById("L1_T5").style.display="none";'."',2000);
										window.parent.document.getElementById('L2_form').reset();
									 </script>";
							}
							else
							{
								//ע��ʧ�ܣ����ݿ����
								$judge= 3;
								echo "<script>
										window.parent.document.getElementById('L2_T6').style.display='block';
										setTimeout('".'window.parent.document.getElementById("L2_T6").style.display="none";'."',2000);										
									 </script>";	
							}
						}
					}
					else
					{
						//�Ŷ������������
						$judge= 4;
						echo "<script>
									window.parent.document.getElementById('L2_T2_C1').value='';
									window.parent.document.getElementById('L2_T2_C1').placeholder='Error!';
									window.parent.document.getElementById('L2_T2_C1').style.backgroundColor='RGB(55,96,146)';
							  </script>";
					}
				}
				break;
			}
		}
		if($judge == 0)
		{
			//�ŶӲ�����
			if($_POST['L2_T2_H']==1)
			{
				$teamsconut_sql = "SELECT * FROM teams";
				$teamsconut_result = mysql_query($teamsconut_sql,$con);
				$teamsconut= mysql_num_rows($teamsconut_result);
				$teamsconut= $teamsconut+1;
				//�û���ע���Ŷ�
				$table= "t".$teamsconut;
				//������Ա��
				$create= "CREATE TABLE ".$table."(id int(11) not null auto_increment, name varchar(255), code varchar(255), email varchar(255), realname varchar(255), telephone varchar(255), address varchar(255), primary key(id) )";
				//�����Ա��
				$insert= "INSERT INTO ".$table."(name,code,email,realname,telephone,address)VALUES( '$_POST[L2_T2_U]', '$_POST[L2_T2_C3]', '$_POST[L2_T2_M]','0','0','0' )";
				$add= "INSERT INTO teams(name,code,captain,email)VALUES( '$_POST[L2_T2_T]', '$_POST[L2_T2_C1]', '$_POST[L2_T2_U]', '$_POST[L2_T2_M]' )";
				$markAdd= "INSERT INTO mark(mark)VALUES('0')";
				$markCreate= "CREATE TABLE ".$table."mark(id int(11) not null auto_increment, mark int(1), primary key(id) )";				
				$plasmidCreate= "CREATE TABLE ".$table."plasmid(id int(11) not null auto_increment, location text, pname text, type text, sequence text,  plength int(11), bname text,  blength int(11),  hua varchar(255),  conservation varchar(255),  primary key(id) )";		
		

		
				if( mysql_query($create,$con)  && mysql_query($insert,$con) && mysql_query($add,$con)&& mysql_query($markAdd,$con)&& mysql_query($markCreate,$con)&& mysql_query($plasmidCreate,$con) )
				{
					//ע��ɹ�
					
					$s = new SaeStorage();
					$s->write("block",$table.".xml",'<?xml version="1.0" encoding="UTF-8"?><exp_path><enum>1</enum></exp_path>');
					$judge= 2;
					echo "<script>
							window.parent.L2_F1();
							window.parent.document.getElementById('L2_form').reset();
							window.parent.document.getElementById('L1_T5').style.display='block';
							setTimeout('".'window.parent.document.getElementById("L1_T5").style.display="none";'."',2000);					
						 </script>";
				}
				else
				{
					//ע��ʧ�ܣ����ݿ����
					$judge= 3;
					echo "<script>
								window.parent.document.getElementById('L2_T6').style.display='block';
								setTimeout('".'window.parent.document.getElementById("L2_T6").style.display="none";'."',2000);	
						  </script>";			
				}
			}
			else
			{
				//���û���ע���Ա
				$judge= 5;
				echo "<script>
						window.parent.document.getElementById('L2_T2_T').value='';
						window.parent.document.getElementById('L2_T2_T').placeholder='The team is not existing';
						window.parent.document.getElementById('L2_T2_T').style.backgroundColor='RGB(55,96,146)';
					 </script>";
			}
		}
		mysql_close($con);
	?>	
	</body>
</html>

