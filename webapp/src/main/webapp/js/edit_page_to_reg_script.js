$(document).ready(function() {
	$("#AuthorizationRadio").attr('checked', true);
});

function doRegistrationForm() {
	if (!($('#NameMember').length)) {
		$('#PasswordMember').after('<tr id="NameMember"><td align="left">Your Name:</td><td align="left"><input type="text" maxlength="20" minlength="3" name="NameMember" required /></td></tr>');
	}
}

function doAuthorizationForm() {
	if (($('#NameMember').length)) {
		$('#NameMember').remove();
	}
}
