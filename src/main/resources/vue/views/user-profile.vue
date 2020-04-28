<template id="user-profile">
    <div>
        <dl v-if="user">
            <dt>User ID</dt>
            <dd>{{user.id}}</dd>
            <dt>Name</dt>
            <dd>{{user.name}}</dd>
            <dt>Email</dt>
            <dd>{{user.email}}</dd>
            <dt>Birthday</dt>
            <dd>{{user.userDetails.dateOfBirth}}</dd>
            <dt>Salary</dt>
            <dd>{{user.userDetails.salary}}</dd>
        </dl>
    </div>
</template>
<script>
    Vue.component("user-profile", {
        template: "#user-profile",
        data: () => ({
            user: null,
        }),
        created() {
            const userId = this.$javalin.pathParams["userId"];
            fetch(`/api/users/${userId}`)
                .then(res => res.json())
                .then(res => this.user = res)
                .catch(() => alert("Error while fetching user"));
        }
    });
</script>
<style>
dl {
				margin: 0;
			}
			dl:after {
				content: ' ';
				display: table;
				clear: both;
			}
				dt,
				dd {
					float: left;
					margin: 0.5em 0;
				}
				dt {
					clear: left;
					padding-right: 1em;
					width: 30%;
					box-sizing: border-box; /* исключительно для padding'a */
				}
				dd {
					width: 70%;
				}
</style>