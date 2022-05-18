WITH

    LatestDatesInternal AS
        (SELECT MAX(irc.StartDate) StartDate, irc.CounterpartyID
         FROM dbo.ctl_InternalRatingCounterparty AS irc
         GROUP BY irc.CounterpartyID),

    FinalInternalRatings AS
        (SELECT
             cp.ID CounterpartyID,
             cp.Name,
             rv.Name AS Rating,
             irc.StartDate AS RatingDate,
             irc.Analyst,
             cp.isSRK,
             cp.intraGroup,
             cp.SubsidiaryLimit,
             cp.UKZ,
             cp.Causes,
             rv.ID AS RatingValue
         FROM
             dbo.ctl_InternalRatingCounterparty AS irc
                 INNER JOIN LatestDatesInternal AS ldi ON (irc.CounterpartyID = ldi.CounterpartyID
                 AND irc.StartDate = ldi.StartDate)
                 INNER JOIN dbo.ctl_RatingValues rv ON (irc.RatingValueID = rv.ID)
                 INNER JOIN dbo.ctl_Counterparties AS cp ON (cp.ID = irc.CounterpartyID)
        )

SELECT * FROM FinalInternalRatings
where isSRK = 'true'
order by RatingValue