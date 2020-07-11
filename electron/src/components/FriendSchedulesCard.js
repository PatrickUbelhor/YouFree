import '../css/FriendSchedulesCard.css';
import React from 'react';
import Schedule from './Schedule';
import {
	Card,
	CardContent
} from '@material-ui/core';

function FriendSchedulesCard(props) {

	if (props.friends == null) {
		return "Loading friends' schedules...";
	}

	if (props.friends.length === 0) {
		return null;
	}

	props.friends.sort((a, b) => a.user.username.localeCompare(b.user.username));

	let combined = [];
	for (let i = 0; i < props.friends.length; i++) {
		let nameDiv = <div className="name" key={i}>{props.friends[i].user.username}</div>;
		let scheduleDiv = (
			<div className="schedule">
				<Schedule key={i} schedule={props.friends[i].schedule}/>
			</div>
		);

		combined.push(nameDiv, scheduleDiv);
	}

	return (
		<Card className="friend-schedules-card" elevation={4}>
			<CardContent>
				<div id="schedules-grid">
					{combined}
				</div>
			</CardContent>
		</Card>
	);

}

export default FriendSchedulesCard;
