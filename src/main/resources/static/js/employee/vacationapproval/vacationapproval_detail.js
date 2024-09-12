function redirectList(){
	  const memberNo = document.getElementById('member_no').value;
	  console.log(memberNo);
	  location.href = `/employee/vacationapproval/list/${memberNo}`;
}