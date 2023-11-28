Projeto desenvolvido por Alan Melo, para realizar a integração com um site de resgates de animais perdidos "Pets Home" desenvolvido por Gislaine Melo. 
A intenção é que a pessoa ao clicar no chat instale o app para se comunicar com algum colaborador do site, para comunicar a perda de algum animal ou ter achado um...

Densenvolvido no Android Studio.

Linguagem utilizada: Java

Banco de dados: Firebase
Foram utilizados os servidos de:
1- Firebase Authentication - Para criação e autenticação
2- Firebase Firestore - Para o armazentamento dos usuário e conversas.
3- Firebase Storage - Para o armazenamento das fotos dos usuários.


# Configurar o período de tempo (última semana)
$startDate = (Get-Date).AddDays(-7)

# Obter eventos de log relevantes
$events = Get-EventLog -LogName Security -After $startDate |
          Where-Object { $_.EventID -eq 4624 -and $_.Message -like "*Logon Type: 3*" }

# Processar os eventos e exibir informações
foreach ($event in $events) {
    $xml = [xml]$event.ToXml()
    $userName = $xml.Event.EventData.Data | Where-Object { $_.Name -eq 'TargetUserName' } | Select-Object -ExpandProperty '#text'
    $computerName = $xml.Event.EventData.Data | Where-Object { $_.Name -eq 'WorkstationName' } | Select-Object -ExpandProperty '#text'
    
    Write-Host "Usuário: $userName, Computador: $computerName, Hora: $($event.TimeGenerated)"
}
