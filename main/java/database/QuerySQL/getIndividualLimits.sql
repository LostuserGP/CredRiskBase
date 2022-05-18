SELECT	Id,
        Department,
		CounterpartyLimit,
		Comment,
		GarantMinRaiting,
		GarantLimit,
		Counterparty_id
FROM    ctl_IndividualLimits
WHERE   ctl_IndividualLimits.Counterparty_id = ?